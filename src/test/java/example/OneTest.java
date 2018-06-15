package example;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class OneTest {

  public static final File OUT_FILE = new File("out.txt");

  @Before
  public void beforeFoo(){
    if(OUT_FILE.exists()){
      OUT_FILE.delete();
    }
  }

  private List<String> readOutFile() {
    FileInputStream is;
    List<String> list = new ArrayList<String>();
    try {
      if (OUT_FILE.length() != 0) {
        is = new FileInputStream(OUT_FILE);
        InputStreamReader streamReader = new InputStreamReader(is);
        BufferedReader reader = new BufferedReader(streamReader);
        String line;
        while ((line = reader.readLine()) != null) {
          list.add(line);
        }
        reader.close();
        is.close();
      }

    } catch (Exception e) {
      e.printStackTrace();
    }
    return list;

  }

  @Test
  public void testFoo() {
    One one = new One();
    one.foo();
    List<String> list = readOutFile();
    System.out.println(list);
    assertEquals("freets", list.get(2));
  }

}