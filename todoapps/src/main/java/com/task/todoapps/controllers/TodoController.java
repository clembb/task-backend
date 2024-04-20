package com.task.todoapps.controllers;

import com.task.todoapps.dtos.ResponseDto;
import com.task.todoapps.model.Todo;
import com.task.todoapps.services.TodoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/")
public class TodoController {
    private TodoService todoService;

    public TodoController(TodoService todoService) {
        this.todoService = todoService;
    }


    @GetMapping("/todos")
    public List<Todo> findTodos(){
        return todoService.findall();
    }

    @GetMapping("/todos/{todoid}")
    public Todo getTodo(@PathVariable int todoid) {

        Todo thetodo = todoService.findByid(todoid);

        if (thetodo == null) {
            throw new RuntimeException("Employee id not found - " + todoid);
        }

        return thetodo;
    }


    @PostMapping("/todos")
    public Todo addTodo(@RequestBody Todo thetodo){
        thetodo.setTodoid(0L);
        Todo todo=todoService.save(thetodo);

        return todo;

    }

    @PutMapping("/todos")
    public Todo updateTodo(@RequestBody Todo todos) {

        Todo todo = todoService.save(todos);
        return todo;
    }

    @DeleteMapping("/todo/{todoid}")
    public ResponseEntity<ResponseDto> deleteTodo(@PathVariable int todoid) {

        Todo todo = todoService.findByid(todoid);

        // throw exception if null

        if (todo == null) {
            throw new RuntimeException("User id not found - " + todo);
        }

            todoService.deleteById(todoid);

        return new ResponseEntity<>(new ResponseDto("Task deleted successfully"), HttpStatus.OK);
    }
}
