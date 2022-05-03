package main.model;

import main.model.tasks.Task;


/*
 Сергей, мне очень понравилась идея формировать сообщение об пересечении по времени прям в этом классе, так код
 в месте, где я использую этот класс стал гораздо лучше читаться. Ты мне предложил проблемные таски в сет собрать
 и потом по всем сообщение сделать. Но у меня этот класс задуман формировать сообщение для исключения.
 Я рискнул сделать вот так. Хочется чтобы при попытке создать
 таску с недопустимым временем, пользователю сразу же кидалось исключение TaskTimeException с предупреждением, которое
 в этом классе сформируется. Смысл вроде: вы хотите создать задачу с временем которое пересекается с "тут сообщение
 сделанное этим классом где видно пересекаемое время". И как бы пользователь должен сразу другое время задать, прежде,
 чем ему приложение разрешит двигаться дальше. Надеюсь, так норм будет)
*/
public class IntersectionSearchResult {
    private final boolean intersect;
    private final Task intersectedTask;

    public IntersectionSearchResult(boolean intersect, Task intersectedTask) {
        this.intersect = intersect;
        this.intersectedTask = intersectedTask;
    }

    public String generateMessage() {
        return "Пересечение по времени с " +
                intersectedTask.getId() + " startTime: " + intersectedTask.getStartTime() + "\n";
    }

    public boolean isIntersection() {
    return intersect;
    }
}