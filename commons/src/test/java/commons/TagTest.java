package commons;

import org.junit.jupiter.api.Test;

import java.awt.*;

import static org.junit.jupiter.api.Assertions.*;

public class TagTest {

    @Test
    public void testGettersAndSetters() {
        Tag tag = new Tag("John", Color.black);

        tag.setId(123);
        tag.setEvent(new Event());

        assertNotNull(tag.getName());
        assertNotNull(tag.getColor());
        assertNotNull(tag.getName());
        assertEquals(tag.getId(), 123);
    }

    @Test
    public void testEqualsAndHash() {

        Tag tag1 = new Tag("Tag", Color.pink);
        Tag tag2 = new Tag("Tag", Color.pink);
        Tag tag3 = new Tag("Tag", Color.blue);

        assertEquals(tag1, tag2);
        assertNotEquals(tag1, tag3);

        assertEquals(tag1.hashCode(), tag2.hashCode());
        assertNotEquals(tag1.hashCode(), tag3.hashCode());
    }

    @Test
    public void testToString() {
        Tag tag = new Tag("tag", Color.black);
        tag.setId(123);
        tag.setEvent(new Event());

        String string = "ID: 123 name: tag color: java.awt.Color[r=0,g=0,b=0]";
        assertEquals(tag.toString(), string);
    }

}
