package hexlet.code.dto.taskstatus;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskStatusCreateDTO {

    private String name;

    private String slug;
}
