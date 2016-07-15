package org.onosproject.yangutils.datamodel;


import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import org.onosproject.yangutils.datamodel.exceptions.DataModelException;
import org.onosproject.yangutils.datamodel.utils.Parsable;
import org.onosproject.yangutils.datamodel.utils.ResolvableStatus;
import org.onosproject.yangutils.datamodel.utils.YangConstructType;

public class YangCompilerAnnotation
        implements Parsable, YangXPathResolver, Resolvable, Serializable {
    private static final long serialVersionUID = 806201602L;

    private YangAppDataStructure yangAppDataStructure;

    private String prefix;

    private String path;

    List<YangAtomicPath> atomicPathList = new LinkedList<>();

    private ResolvableStatus resolvableStatus;

    public YangAppDataStructure getYangAppDataStructure() {
        return yangAppDataStructure;
    }

    public void setYangAppDataStructure(YangAppDataStructure yangAppDataStructure) {
        this.yangAppDataStructure = yangAppDataStructure;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public List<YangAtomicPath> getAtomicPathList() {
        return atomicPathList;
    }

    public void setAtomicPathList(List<YangAtomicPath> atomicPathList) {
        this.atomicPathList = atomicPathList;
    }


    @Override
    public YangConstructType getYangConstructType() {
        return YangConstructType.COMPILER_ANNOTATION_DATA;
    }

    @Override
    public void validateDataOnEntry()
            throws DataModelException {

    }

    @Override
    public void validateDataOnExit()
            throws DataModelException {

    }

    @Override
    public ResolvableStatus getResolvableStatus() {
        return resolvableStatus;
    }

    @Override
    public void setResolvableStatus(ResolvableStatus resolvableStatus) {
        this.resolvableStatus = resolvableStatus;

    }

    @Override
    public Object resolve()
            throws DataModelException {

        return null;
    }
}
