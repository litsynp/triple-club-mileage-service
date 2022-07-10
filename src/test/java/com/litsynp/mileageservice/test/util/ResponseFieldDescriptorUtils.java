package com.litsynp.mileageservice.test.util;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.subsectionWithPath;

import java.util.stream.Stream;
import org.springframework.restdocs.payload.FieldDescriptor;

public class ResponseFieldDescriptorUtils {

    private static final FieldDescriptor[] pageDescriptorsIgnored = {
            subsectionWithPath("pageable").ignored(),
            fieldWithPath("last").ignored(),
            fieldWithPath("totalPages").ignored(),
            fieldWithPath("totalElements").ignored(),
            subsectionWithPath("sort").ignored(),
            fieldWithPath("size").ignored(),
            fieldWithPath("number").ignored(),
            fieldWithPath("first").ignored(),
            fieldWithPath("numberOfElements").ignored(),
            fieldWithPath("empty").ignored()
    };

    public static FieldDescriptor[] withPageDescriptorsIgnored(
            FieldDescriptor... fieldDescriptors) {
        return Stream.of(fieldDescriptors, pageDescriptorsIgnored)
                .flatMap(Stream::of)
                .toArray(FieldDescriptor[]::new);
    }
}
