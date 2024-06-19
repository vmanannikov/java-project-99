package hexlet.code.dto.task;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.openapitools.jackson.nullable.JsonNullable;

import java.util.List;

@Getter
@Setter
public class TaskUpdateDTO {

    @NotBlank
    private JsonNullable<String> title;

    private JsonNullable<Long> index;

    private JsonNullable<String> content;

    @JsonProperty("assignee_id")
    private JsonNullable<Long> assigneeId;

    @NotNull
    private JsonNullable<String> status;

    private List<Long> taskLabelIds;

}
