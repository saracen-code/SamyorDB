package com.samyorBot.util;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;

public class ModChecker {

    /**
     * Checks if the given member is considered an admin.
     *
     * @param member The Discord member to check.
     * @return true if member is an admin, false otherwise.
     */
    public static boolean isAdmin(Member member) {
        if (member == null) return false;

        // Standard Discord permission check
        if (member.hasPermission(Permission.ADMINISTRATOR)) {
            return true;
        }

        return false;
    }

    public static void performSensitiveAction(Member member) {
        if (!ModChecker.isAdmin(member)) {
            throw new IllegalStateException("❌ Unauthorized: User is not an admin.");
        }
    }
}
