package io.sls.resources.rest.patch;

import lombok.Getter;
import lombok.Setter;

/**
 * @author ginccc
 */

@Getter
@Setter
public class PatchInstruction<T> {
    public enum PatchOperation {
        SET,
        DELETE
    }

    private PatchOperation operation;
    private T document;
}
