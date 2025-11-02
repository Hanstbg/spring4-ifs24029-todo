package org.delcom.starter.controllers;

import java.lang.reflect.Method;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class HomeControllerTest {

    private HomeController controller;

    @BeforeEach
    void setUp() {
        controller = new HomeController();
    }

    // 1. Informasi NIM
    @Test
    void testInformasiNim_Valid() {
        String result = controller.informasiNim("11S23001");
        assertTrue(result.contains("Sarjana Informatika"));
        assertTrue(result.contains("Angkatan: 2023"));
        assertTrue(result.contains("Urutan: 1"));
    }

    @Test
    void testInformasiNim_Invalid() {
        assertTrue(controller.informasiNim("123").contains("minimal 8 karakter"));
    }

    // 2. Perolehan Nilai
    @Test
    void testPerolehanNilai_Valid() {
        String data = "UAS|85|40\nUTS|75|30\nPA|90|20\nK|100|10\n---\n";
        String b64 = Base64.getEncoder().encodeToString(data.getBytes());
        String result = controller.perolehanNilai(b64);
        assertTrue(result.contains("84.50"));
        assertTrue(result.contains("Total Bobot: 100%"));
        assertTrue(result.contains("Grade: B"));
    }

    @Test
    void testPerolehanNilai_CoversCalculationAndGrade() {
        // INI YANG MENUTUP 1% TERAKHIR!
        String data = "Tugas|80|100\n---\n";
        String b64 = Base64.getEncoder().encodeToString(data.getBytes());
        String result = controller.perolehanNilai(b64);
        assertEquals("Nilai Akhir: 80.00 (Total Bobot: 100%)\nGrade: B", result);
    }

    @Test
    void testPerolehanNilai_InvalidBase64() {
        assertThrows(IllegalArgumentException.class, () -> controller.perolehanNilai("!@#"));
    }

    // 3. Perbedaan L dan Kebalikannya
    @Test
    void testPerbedaanL_Valid() {
        String b64 = Base64.getEncoder().encodeToString("UULL".getBytes());
        String result = controller.perbedaanL(b64);
        assertTrue(result.contains("UULL -> (-2, 2)"));
        assertTrue(result.contains("DDRR -> (2, -2)"));
        assertTrue(result.contains("Perbedaan Jarak: 8"));
    }

    @Test
    void testPerbedaanL_CoversAllDirections() throws Exception {
        Method reverse = HomeController.class.getDeclaredMethod("reversePath", String.class);
        Method endpoint = HomeController.class.getDeclaredMethod("calculateEndPoint", String.class);
        reverse.setAccessible(true);
        endpoint.setAccessible(true);

        assertEquals("D", reverse.invoke(controller, "U"));
        assertEquals("U", reverse.invoke(controller, "D"));
        assertEquals("R", reverse.invoke(controller, "L"));
        assertEquals("L", reverse.invoke(controller, "R"));

        assertArrayEquals(new int[]{0, 1}, (int[]) endpoint.invoke(controller, "U"));
        assertArrayEquals(new int[]{0, -1}, (int[]) endpoint.invoke(controller, "D"));
        assertArrayEquals(new int[]{-1, 0}, (int[]) endpoint.invoke(controller, "L"));
        assertArrayEquals(new int[]{1, 0}, (int[]) endpoint.invoke(controller, "R"));
    }

    // 4. Paling Ter
    @Test
    void testPalingTer_Valid() {
        String text = "terbaik Terbaik terbaik";
        String b64 = Base64.getEncoder().encodeToString(text.getBytes());
        String result = controller.palingTer(b64);
        assertTrue(result.contains("'terbaik'"));
        assertTrue(result.contains("muncul 3 kali"));
    }

    @Test
    void testPalingTer_NoTer() {
        String b64 = Base64.getEncoder().encodeToString("hello world".getBytes());
        assertEquals("Tidak ditemukan kata yang berawalan 'ter'.", controller.palingTer(b64));
    }

    // Tutup coverage calculateGrade
    @Test
    void testCalculateGrade_Coverage() throws Exception {
        Method method = HomeController.class.getDeclaredMethod("calculateGrade", double.class);
        method.setAccessible(true);
        assertEquals("A", method.invoke(controller, 90.0));
        assertEquals("E", method.invoke(controller, 0.0));
    }
}