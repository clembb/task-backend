package com.task.todoapps.services;

import com.task.todoapps.model.Todo;
import com.task.todoapps.repository.TodoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TodoService {

    public TodoRepository todoRepository;

    public TodoService(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }
    public List<Todo> findall(){
        return todoRepository.findAll();
    }

    public Todo findByid(int id){
        Optional<Todo> todo =todoRepository.findById(id);
        Todo todos=null;
        if(todo.isPresent()){
            todos=todo.get();
        }else{
            throw new RuntimeException("not found"+id);
        }
        return todos;
    }
    public Todo save(Todo todo){
        return todoRepository.save(todo);
    }

    public void delete(Todo todo){
        todoRepository.delete(todo);
    }
    public void deleteById(int theId)
    {
        todoRepository.deleteById(theId);
    }

}
