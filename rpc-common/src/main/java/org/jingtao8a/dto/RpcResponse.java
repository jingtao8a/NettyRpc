package org.jingtao8a.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class RpcResponse implements Serializable {

    /* response code */
    private Integer code;

    /* response message */
    private String message;

    /* response body */
    private Object data;
}
