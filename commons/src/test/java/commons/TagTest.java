package commons;

import org.junit.jupiter.api.Test;

import java.awt.*;

import static org.junit.jupiter.api.Assertions.*;

public class TagTest {

    @Test
    public void testGettersAndSetters() {
        Tag tag = new Tag("John", new Color(255,0,0,255));

        tag.setId(123L);
        tag.setEvent(new Event());

        assertNotNull(tag.getName());
        assertNotNull(tag.getColor());
        assertNotNull(tag.getName());
        assertEquals(tag.getId(), 123);
    }

    @Test
    public void testEqualsAndHash() {
        Color red = new Color(255,0,0,255);

        Tag tag1 = new Tag("Tag", red);
        Tag tag2 = new Tag("Tag", red);
        Tag tag3 = new Tag("Tag", new Color(0,0,255,255));

        assertEquals(tag1, tag2);
        assertNotEquals(tag1, tag3);

        assertEquals(tag1.hashCode(), tag2.hashCode());
        assertNotEquals(tag1.hashCode(), tag3.hashCode());
    }

    @Test
    public void testToString() {
        Color red = new Color(255,0,0,255);
        Tag tag = new Tag("tag", red);
        tag.setId(123L);
        tag.setEvent(new Event());

        String string = "ID: 123 name: tag color: " + red.toString();
        assertEquals(tag.toString(), string);
    }

}
