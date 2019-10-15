package com.atguigu.gmall.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString
public class UserBean {
    String id;
    String member_level_id ;
    String username;
    String password ;
    String nickname ;
    String  phone  ;
    String status ;
    String create_time ;
    String icon ;
    String gender ;
    String birthday;
    String city ;
    String job ;
    String personalized;
    String source_uid ;
    String source_type;
    String integration ;
    String growth;
    String luckey_count;
    String access_token;
    String access_code;
    String history_integration;

}
