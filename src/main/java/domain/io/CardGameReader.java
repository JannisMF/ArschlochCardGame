package domain.io;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

@Slf4j
public class CardGameReader extends BufferedReader {
    public CardGameReader(Reader in) {
        super(in);
    }


    public String readLine() throws IOException {
        String input = super.readLine();
        String logInput = "Read: " + input;
        log.debug(logInput);
        return input;
    }


}
