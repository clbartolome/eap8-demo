package dev.resteasy.examples.resources;

import dev.resteasy.examples.data.ContactRegistry;
import dev.resteasy.examples.model.Contact;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;
import jakarta.ws.rs.core.UriInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ContactResourceTest {

    private ContactResource contactResource;
    private ContactRegistry contactRegistry;
    private UriInfo uriInfo;

    @BeforeEach
    void setUp() throws Exception {
        contactRegistry = mock(ContactRegistry.class);
        uriInfo = mock(UriInfo.class);
        contactResource = new ContactResource();

        // Inject mocks using reflection
        var contactRegistryField = ContactResource.class.getDeclaredField("contactRegistry");
        contactRegistryField.setAccessible(true);
        contactRegistryField.set(contactResource, contactRegistry);

        var uriInfoField = ContactResource.class.getDeclaredField("uriInfo");
        uriInfoField.setAccessible(true);
        uriInfoField.set(contactResource, uriInfo);

        // Setup mock URI builder
        UriBuilder mockBuilder = mock(UriBuilder.class);
        when(uriInfo.getBaseUriBuilder()).thenReturn(mockBuilder);
        when(mockBuilder.path(anyString())).thenReturn(mockBuilder);
        when(mockBuilder.build()).thenReturn(new URI("http://localhost/contact/1"));
    }

    private Contact createContact(Long id) {
        Contact contact = new Contact();
        contact.setId(id);
        contact.setFirstName("Jane");
        contact.setLastName("Doe");
        contact.setEmail("jane.doe@example.com");
        contact.setPhoneNumber("1234567890");
        contact.setCompanyName("Red Hat");
        return contact;
    }

    @Test
    void testGetAllContacts() {
        Contact contact = createContact(1L);
        List<Contact> dummyList = Collections.singletonList(contact);
        when(contactRegistry.getContacts()).thenReturn(dummyList);

        Response response = contactResource.get();
        assertEquals(200, response.getStatus());
        assertEquals(dummyList, response.getEntity());
    }

    @Test
    void testGetContactFound() {
        Contact contact = createContact(1L);
        when(contactRegistry.getContactById(1L)).thenReturn(contact);

        Response response = contactResource.get(1L);
        assertEquals(200, response.getStatus());
        assertEquals(contact, response.getEntity());
    }

    @Test
    void testGetContactNotFound() {
        when(contactRegistry.getContactById(999L)).thenReturn(null);

        Response response = contactResource.get(999L);
        assertEquals(404, response.getStatus());
    }

    @Test
    void testAddContact() {
        Contact contact = createContact(1L);
        when(contactRegistry.add(contact)).thenReturn(contact);

        Response response = contactResource.add(contact);
        assertEquals(201, response.getStatus());
    }

    @Test
    void testEditContact() {
        Contact contact = createContact(1L);
        when(contactRegistry.update(contact)).thenReturn(contact);

        Response response = contactResource.edit(contact);
        assertEquals(201, response.getStatus());
    }

    @Test
    void testDeleteContactFound() {
        Contact contact = createContact(1L);
        when(contactRegistry.delete(1L)).thenReturn(contact);

        Response response = contactResource.delete(1L);
        assertEquals(200, response.getStatus());
        assertEquals(contact, response.getEntity());
    }

    @Test
    void testDeleteContactNotFound() {
        when(contactRegistry.delete(999L)).thenReturn(null);

        Response response = contactResource.delete(999L);
        assertEquals(404, response.getStatus());
    }
}
