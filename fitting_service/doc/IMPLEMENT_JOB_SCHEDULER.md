# Implement new job scheduler

## Implementation
Implement the interface defined in [Interface Definition](../main/fitting_service/job_managers/i_job_management.py).

See [SingleNodeJobManagement](../main/fitting_service/job_managers/single_node_job_management.py) or [GridEngineJobManagement](../main/fitting_service/job_managers/grid_engine_job_management.py) for reference implementations.

## Registration
- Implement the interface 
- Inside the [settings](../main/fitting_service/settings.py) set the ```JOB_MANAGEMENT_TYPE``` to your JobManager type
