package org.onosproject.yangutils.datamodel;

/**
 * Created by root1 on 8/7/16.
 */
public enum YangDataStructure {

    QUEUE,

    LIST;

    /**
     * Returns YANG data type for corresponding type name.
     *
     * @param name type name from YANG file.
     * @return YANG data type for corresponding type name.
     */
    public static YangDataStructure getType(String name) {
        name = name.replace("\"", "");
        for (YangDataStructure dataStructure : values()) {
            if (dataStructure.name().toLowerCase().equals(name)) {
                return dataStructure;
            }
        }
        return null;
    }
}
