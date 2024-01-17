package org.jingtao8a.dto;

import lombok.*;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@ToString
public class RpcResponse implements Serializable {

    /* response code */
    private Integer code;

    /* response message */
    private String message;

    /* response body */
    private Object data;
}
