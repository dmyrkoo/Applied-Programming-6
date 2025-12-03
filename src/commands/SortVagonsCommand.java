package commands;

import services.SkladService;

public class SortVagonsCommand implements Command {
    private SkladService service;

    public SortVagonsCommand(SkladService service) {
        this.service = service;
    }

    @Override
    public void execute() {
        service.sortuvatyVagony();
    }

    @Override
    public String getDescription() {
        return "3. Сортувати вагони";
    }
}