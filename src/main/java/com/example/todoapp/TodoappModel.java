package com.example.todoapp;

import lombok.Builder;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/*
    Lombok 작동 테스트용 클래스
 */
@Builder
@RequiredArgsConstructor
public class TodoappModel {

    @NonNull
    private String id;
}
