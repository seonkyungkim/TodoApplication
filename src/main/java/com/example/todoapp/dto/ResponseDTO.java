package com.example.todoapp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ResponseDTO<T> {      //HTTP 응답으로 사용할 DTO. 이후 다른 모델의 DTO도 이를 이용하여 리턴할 수 있도록 자바제너릭 이용.
    private String error;
    private List<T> data;      //주로 리스트를 반환하는 경우가 많으므로 데이터를 리스트로 반환하도록 작성
}
