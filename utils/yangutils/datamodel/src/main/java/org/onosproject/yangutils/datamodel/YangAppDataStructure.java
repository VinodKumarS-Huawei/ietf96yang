package org.onosproject.yangutils.datamodel;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import org.onosproject.yangutils.datamodel.exceptions.DataModelException;
import org.onosproject.yangutils.datamodel.utils.Parsable;
import org.onosproject.yangutils.datamodel.utils.YangConstructType;

public class YangAppDataStructure
        implements Parsable, Serializable {
    private static final long serialVersionUID = 806201602L;

    private YangDataStructure dataStructure;

    private List<String> keyList;

    private String prefix;

    public YangDataStructure getDataStructure() {
        return dataStructure;
    }

    public void setDataStructure(YangDataStructure dataStructure) {
        this.dataStructure = dataStructure;
    }

    public List<String> getKeyList() {
        return keyList;
    }

    public void setKeyList(List<String> keyList) {
        this.keyList = keyList;
    }

    public void addKey(String key) {

        if (getKeyList() == null) {
            setKeyList(new LinkedList<>());
        }

        getKeyList().add(key);
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    @Override
    public YangConstructType getYangConstructType() {
        return null;
    }

    @Override
    public void validateDataOnEntry()
            throws DataModelException {

    }

    @Override
    public void validateDataOnExit()
            throws DataModelException {

    }
}
