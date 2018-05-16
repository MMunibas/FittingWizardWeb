package ch.unibas.fitting.web.application.calculation.task.messages;

import ch.unibas.fitting.web.application.calculation.execution.messages.StartDefinition;

public class SpawnTask
{
    public String username;
    public String title;
    public String type;
    public Class resultPage;
    public StartDefinition[] calculationsToSpawn;
}
