package com.learningjava.todolist.task;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity(name = "tb_tasks")
public class TaskModal {

    @Id
    @GeneratedValue(generator = "UUID")
    private UUID id;
    private UUID userId;
    @Column(length = 50)
    private String title;
    private String description;
    @ColumnDefault("false")
    private boolean isCompleted;
    private LocalDateTime startAt;
    private LocalDateTime endAt;


    @CreationTimestamp
    private LocalDateTime createdAt;

    public void setTitle(String title) throws Exception {
        if (title.length() > 50) {
            throw new Exception("O campo title deve conter no máximo 50 carácteres");
        }
        this.title = title;
    }

}
