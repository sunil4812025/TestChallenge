package org.swisre.model;

import java.util.ArrayList;
import java.util.List;

public class OrgNode {
    public int id;
    public String name;
    public String role;
    public List<OrgNode> reportees = new ArrayList<>();

    public OrgNode(int id, String name, String role) {
        this.id = id;
        this.name = name;
        this.role = role;
    }
}
