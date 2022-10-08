package fi.haagahelia.serverprog.todomanager.domain.Model.tasks;

public class SortByDueDateAndPriority implements java.util.Comparator<Task> {
    @Override
    public int compare(Task o1, Task o2) {
        return o1.compareToDateAndPriority(o2);
    }
}
