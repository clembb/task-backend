package com.task.todoapps.model;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Date;
@Data
@NoArgsConstructor
@Builder
@Entity
@Table(name = "todo")
public class Todo {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long todoid;
        @Column(name="title")
        private String title;
        @Column(name="description")
        private String description;
        @Column(name="date")
        private LocalDate date;


        public Todo(Long todoid, String title, String description, LocalDate date) {
            this.todoid = todoid;
            this.title = title;
            this.description = description;
            this.date = date;
        }

}
