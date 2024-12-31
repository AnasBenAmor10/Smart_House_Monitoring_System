package tn.cot.smarthome;

import static org.junit.jupiter.api.Assertions.assertEquals;

import jakarta.inject.Inject;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Run integration tests with Arquillian to be able to test CDI beans
 */
@ExtendWith(ArquillianExtension.class)
public class  apiServiceIT {

    @Deployment
    public static WebArchive createTestArchive() {
        return ShrinkWrap.create(WebArchive.class, "apiServiceIT.war")
                .addClass(apiService.class);
    }

    @Inject
    apiService service;

    @Test
    public void testService() {
        String result = service.hello("World");
        assertEquals("Hello 'World'.", result);

        result = service.hello("Monde");
        assertEquals("Hello 'Monde'.", result);
    }
}