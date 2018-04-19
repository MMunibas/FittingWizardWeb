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
        xml = sp.check_output([self.qstat_path, "-xml"])
        xml = xmltodict.parse(xml)
        queue = []
        if xml['job_info']['queue_info'] is not None:
            for job in xml['job_info']['queue_info']['job_list']:
                queue.append(job)
        return queue

    def list_running_jobs(self):
        q = self._parse()
        print("queued jobs: ", q)
        j = [job["JB_job_number"] for job in q]
        print("list of job ids: ", j)
        return j

    def schedule_new_job(self, job_name, command):
        regex = re.compile('(?:.*\n)*Your job (\d*) \("(.*)"\).*')
        output = sp.check_output([self.qsub_path, "-N", job_name, command]).decode("utf-8")
        match = regex.match(output)
        if match:
            job_id = match.group(1)
            job_name = match.group(2)
            return job_id # , job_name
        return None

    def job_status(self, job_id):
        return [job["@state"] for job in self._parse() if job["JB_job_number"] == job_id][0]

    def cancel_job(self, job_id):
        sp.check_call([self.qdel_path, job_id])

