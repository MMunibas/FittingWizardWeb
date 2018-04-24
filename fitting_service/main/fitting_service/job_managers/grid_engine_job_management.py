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
        xml = xmltodict.parse(raw_xml)
        queue = []
        if xml['job_info']['queue_info'] is not None:
            d = [dict(j) for j in xml['job_info']['queue_info']['job_list']]
            for job in d:
                queue.append(job)
        return queue, raw_xml

    def list_running_job_ids(self):
        q, r = self._parse()
        try:
            print("queued jobs: ", q)
            j = [job["JB_job_number"] for job in q]
            print("list of job ids: ", j)
            return j
        except Exception as e:
            with open("qstat-xml.txt", "w") as debug_dump_file:
                debug_dump_file.write(r)
                debug_dump_file.write("\n---------------------------------------------------\n")
                debug_dump_file.write(q)
                debug_dump_file.write("\n---------------------------------------------------\n")
                debug_dump_file.write(type(xmltodict.OrderedDict))
                debug_dump_file.write("\n---------------------------------------------------\n")
                debug_dump_file.write(e)
            raise e

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
        if len(filtered)>0:
            matches = [job["@state"] for job in filtered]
            return matches[0]
        return None

    def cancel_job(self, job_id):
        sp.check_call([self.qdel_path, job_id])

