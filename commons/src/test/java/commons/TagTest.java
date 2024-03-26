package commons;

import org.junit.jupiter.api.Test;

import java.awt.*;

import static org.junit.jupiter.api.Assertions.*;

public class TagTest {

    @Test
    public void testGettersAndSetters() {
        Tag tag = new Tag("John", "HEXcolor");

        tag.setId(123L);
        tag.setEvent(new Event());

        assertNotNull(tag.getName());
        assertNotNull(tag.getColor());
        assertNotNull(tag.getName());
        assertEquals(tag.getId(), 123);
    }

    @Test
    public void testEqualsAndHash() {
        String red = "HEXcolor1";

        Tag tag1 = new Tag("Tag", red);
        Tag tag2 = new Tag("Tag", red);
        Tag tag3 = new Tag("Tag", "HEXcolor");

        assertEquals(tag1, tag2);
        assertNotEquals(tag1, tag3);

        assertEquals(tag1.hashCode(), tag2.hashCode());
        assertNotEquals(tag1.hashCode(), tag3.hashCode());
    }

    @Test
    public void testToString() {
        String red = "HEXcolor";
        Tag tag = new Tag("tag", red);
        tag.setId(123L);
        tag.setEvent(new Event());

        String string = "ID: 123 name: tag color: " + red.toString();
        assertEquals(tag.toString(), string);
    }

}
