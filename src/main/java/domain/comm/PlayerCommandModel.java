package domain.comm;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class PlayerCommandModel {
    String player;
    String command;
}