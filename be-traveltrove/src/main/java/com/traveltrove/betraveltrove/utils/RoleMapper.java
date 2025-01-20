package com.traveltrove.betraveltrove.utils;

import java.util.HashMap;
import java.util.Map;

public class RoleMapper {
    // Map to store role ID to role name mapping
    private static final Map<String, String> ROLE_ID_TO_NAME = new HashMap<>();

    // Map to store role name to role ID mapping
    private static final Map<String, String> ROLE_NAME_TO_ID = new HashMap<>();

    static {
        // Populate role mappings
        ROLE_ID_TO_NAME.put("rol_n0x6f30TQGgcKJWo", "Admin");
        ROLE_ID_TO_NAME.put("rol_bGEYlXT5XYsHGhcQ", "Customer");
        ROLE_ID_TO_NAME.put("rol_e6pFgGUgGlnHZz1D", "Employee");

        // Reverse mapping for name-to-ID
        ROLE_ID_TO_NAME.forEach((id, name) -> ROLE_NAME_TO_ID.put(name, id));
    }

    /**
     * Get the role name for a given role ID.
     *
     * @param roleId the role ID
     * @return the corresponding role name, or null if not found
     */
    public static String getRoleName(String roleId) {
        return ROLE_ID_TO_NAME.get(roleId);
    }

    /**
     * Get the role ID for a given role name.
     *
     * @param roleName the role name
     * @return the corresponding role ID, or null if not found
     */
    public static String getRoleId(String roleName) {
        return ROLE_NAME_TO_ID.get(roleName);
    }
}
