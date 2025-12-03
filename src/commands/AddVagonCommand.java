package commands;

import services.SkladService;

public class AddVagonCommand implements Command {
    private SkladService service;

    public AddVagonCommand(SkladService service) {
        this.service = service;
    }

    @Override
    public void execute() {
        service.dodatyVagon();
    }

    @Override
    public String getDescription() {
        return "1. Додати вагон";
    }
}