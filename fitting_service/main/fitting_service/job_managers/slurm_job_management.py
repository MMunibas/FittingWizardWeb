import re
import subprocess as sp
from time import sleep
from .i_job_management import IJobManagement


class SlurmJobManagement(IJobManagement):
    def __init__(self, qstat_path="qstat", qsub_path="qsub", qdel_path="qdel"):
        self.qstat_path = qstat_path
        self.qsub_path = qsub_path
        self.qdel_path = qdel_path

    def _parse(self):
        raw_out = sp.check_output([self.qstat_path,"-s", "R,S,PD", "--format=JobID,State,JobName%80"]).decode("utf-8")
        queue = []
        lines=raw_out.split('\n')
        for line in lines:
           words=line.split()
           if len(words) > 2 and words[0].isdigit():
              if words[1] == "RUNNING":
                 state="r"
              elif words[1] == "PENDING":
                 state="qw"
              else:
                 state="s"
              job={'JB_job_number' : words[0], '@state' : state, 'JB_name' : words[2]}
              queue.append(job)
        return queue

    def list_running_job_ids(self):
        q = self._parse()
        j = [job["JB_job_number"] for job in q if not isinstance(job, str)]
        return j

    def schedule_new_job(self, job_name, command):
        regex = re.compile('Submitted batch job (\d*)')
        output = sp.check_output([self.qsub_path, "-J", job_name, command]).decode("utf-8")
        match = regex.match(output)
        if match:
            job_id = match.group(1)
            print("Scheduled job with name '{}' got id '{}'".format(job_name, job_id))
            sleep(5)
            return job_id
        return None

    def job_status(self, job_id):
        parsed = self._parse()
        filtered = [job for job in parsed if job["JB_job_number"] == job_id]
        if len(filtered) > 0:
            matches = [job["@state"] for job in filtered]
            return matches[0]
        return None

    def cancel_job(self, job_id):
        sp.check_call([self.qdel_path, job_id])

