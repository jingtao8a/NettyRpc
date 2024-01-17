package org.jingtao8a.dto;

import lombok.*;

import java.io.Serializable;

/**
 * 自定义协议
 * 0     1     2     3     4        5     6     7     8    9            10             11      12  13 14 15 16
 * +-----+-----+-----+-----+--------+----+----+----+------+-------------+--------------+--------+--+--+--+--+
 * |   magic   code        |version |      full length    | messageType| serializerType|compress| RequestId |
 * +-----------------------+--------+---------------------+-----------+-----------+-----------+------------+
 * |                                                                                                       |
 * |                                         body                                                          |
 * |                                                                                                       |
 * |                                        ... ...                                                        |
 * +-------------------------------------------------------------------------------------------------------+
 * 4B  magic code（魔法数）   1B version（版本）   4B full length（消息长度）    1B messageType（消息类型）
 * 1B compress（压缩类型） 1B serializerType（序列化类型）    4B  requestId（请求的Id）
 * body（object类型数据）
 *
 * */

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@ToString
public class RpcMessage implements Serializable {
    /* 消息类型 */
    private byte messageType;
    /* 序列化类型 */
    private byte serializerType;
    /* 压缩类型 */
    private byte compress;//不使用压缩,该字段无效
    /* 请求的ID */
    private int requestId;
    /* Object 类型数据 */
    private Object body;
}
