package edu.univ.erp.auth;

 // This is a one-time tool to generate a new, clean hash to fix the database.
public class GenerateHash {

    public static void main(String[] args) {
        PasswordService service = new PasswordService();
        String newHash = service.hashPassword("a1");

        System.out.println("--- COPY THE HASH BELOW ---");
        System.out.println(newHash);
        System.out.println("--- END OF HASH ---");
    }
}