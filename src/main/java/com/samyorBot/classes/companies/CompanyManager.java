package com.samyorBot.classes.companies;

import com.samyorBot.classes.companies.Company;

import java.util.*;

public class CompanyManager {

    // All companies by ID
    private static final Map<String, Company> companies = new HashMap<>();

    // Member-to-company mapping
    private static final Map<String, String> memberCompanyMap = new HashMap<>();

    // Pending invites: companyId -> Set of userIds
    private static final Map<String, Set<String>> pendingInvites = new HashMap<>();

    // --- COMPANY LOOKUP ---

    public static Company getCompanyById(String companyId) {
        return companies.get(companyId);
    }

    public static Company getCompanyByMemberId(String userId) {
        String companyId = memberCompanyMap.get(userId);
        return (companyId != null) ? companies.get(companyId) : null;
    }

    // --- REGISTRATION ---

    public static void registerCompany(Company company) {
        companies.put(company.getId(), company);
        for (String memberId : company.getMembers()) {
            memberCompanyMap.put(memberId, company.getId());
        }
    }

    public static void addMemberToCompany(String companyId, String userId) {
        Company company = companies.get(companyId);
        if (company != null) {
            company.addMember(userId);
            memberCompanyMap.put(userId, companyId);
        }
    }

    public static void removeMemberFromCompany(String userId) {
        String companyId = memberCompanyMap.remove(userId);
        if (companyId != null) {
            Company company = companies.get(companyId);
            if (company != null) {
                company.removeMember(userId);
            }
        }
    }

    // --- INVITATIONS ---

    public static void addPendingInvite(String companyId, String userId) {
        pendingInvites.computeIfAbsent(companyId, k -> new HashSet<>()).add(userId);
    }

    public static boolean hasPendingInvite(String companyId, String userId) {
        return pendingInvites.containsKey(companyId) && pendingInvites.get(companyId).contains(userId);
    }

    public static void removePendingInvite(String companyId, String userId) {
        if (pendingInvites.containsKey(companyId)) {
            pendingInvites.get(companyId).remove(userId);
            if (pendingInvites.get(companyId).isEmpty()) {
                pendingInvites.remove(companyId);
            }
        }
    }

    public static Set<String> getPendingInvites(String companyId) {
        return pendingInvites.getOrDefault(companyId, Collections.emptySet());
    }

    // --- DEBUG / DEV TOOL ---

    public static Collection<Company> getAllCompanies() {
        return companies.values();
    }

    public static void clearAll() {
        companies.clear();
        memberCompanyMap.clear();
        pendingInvites.clear();
    }
}
