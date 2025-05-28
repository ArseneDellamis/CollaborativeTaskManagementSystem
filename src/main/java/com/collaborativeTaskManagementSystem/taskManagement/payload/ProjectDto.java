package com.collaborativeTaskManagementSystem.taskManagement.payload;

import com.collaborativeTaskManagementSystem.taskManagement.model.constants.Status;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProjectDto {
    
    @NotBlank(message = "Project name is required")
    @Size(min = 3, max = 100, message = "Project name must be between 3 and 100 characters")
    private String projectName;
    
    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String projectDescription;
    
    @NotBlank(message = "Status is required")
    private String status;
    
    // Custom validation method to check if status is valid
    public boolean isValidStatus() {
        try {
            if (status != null) {
                Status.valueOf(status.toUpperCase());
                return true;
            }
            return false;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
    
    // Get valid status values as string for error messages
    public static String getValidStatusValues() {
        return String.join(", ", java.util.Arrays.stream(Status.values())
                .map(Enum::name)
                .toArray(String[]::new));
    }
}
