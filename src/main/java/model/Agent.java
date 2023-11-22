package model;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Agent {
    private int agentId;
    private String name;
    private String phone;
    private String fax;
    private String email;
    private String username;
    private String password;

}
