import re
import subprocess as sp

import xmltodict

from .i_job_management import IJobManagement


class GridEngineJobManagement(IJobManagement):
    def __init__(self, qstat_path="qstat", qsub_path="qsub", qdel_path="qdel"):
        self.qstat_path = qstat_path
        self.qsub_path = qsub_path
        self.qdel_path = qdel_path

    def _parse(self):
        raw_xml = sp.check_output([self.qstat_path, "-xml"])
        xml = xmltodict.parse(raw_xml, force_list={'job_list': 'job_list'})
        queue = []
        if 'queue_info' in xml['job_info'] and xml['job_info']['queue_info'] is not None:
            d = [j for j in xml['job_info']['queue_info']['job_list']]
            for job in d:
                queue.append(job)
        if 'job_info' in xml['job_info'] and xml['job_info']['job_info'] is not None:
            d = [j for j in xml['job_info']['job_info']['job_list']]
            for job in d:
                queue.append(job)
        return queue, raw_xml

    def list_running_job_ids(self):
        q, r = self._parse()
        j = [job["JB_job_number"] for job in q if not isinstance(job, str)]
        return j

    def schedule_new_job(self, job_name, command):
        regex = re.compile('(?:.*\n)*Your job (\d*) \("(.*)"\).*')
        output = sp.check_output([self.qsub_path, "-N", job_name, command]).decode("utf-8")
        match = regex.match(output)
        if match:
            job_id = match.group(1)
            job_name = match.group(2)
            print("Scheduled job with name '{}' got id '{}'".format(job_name, job_id))
            return job_id
        return None

    def job_status(self, job_id):
        parsed = self._parse()[0]
        filtered = [job for job in parsed if job["JB_job_number"] == job_id]
        if len(filtered) > 0:
            matches = [job["@state"] for job in filtered]
            return matches[0]
        return None

    def cancel_job(self, job_id):
        sp.check_call([self.qdel_path, job_id])

