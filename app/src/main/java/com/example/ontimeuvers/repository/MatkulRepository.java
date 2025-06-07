package com.example.ontimeuvers.repository;

import com.example.ontimeuvers.entity.Matkul;
import com.example.ontimeuvers.entity.User;
import com.example.ontimeuvers.model.AddUsersMatkul;
import com.example.ontimeuvers.model.EditMatkulRequest;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface MatkulRepository {

    CompletableFuture<Void> editUserMatkul(EditMatkulRequest request);

    CompletableFuture<List<User>> addUsersMatkul(AddUsersMatkul request);

}
