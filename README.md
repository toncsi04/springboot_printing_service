```mermaid
gantt
title 3D Printing Service - Projekt ütemterv
dateFormat  YYYY-MM-DD
section Tervezés & Projektmenedzsment
Követelmény specifikáció      :done,    des1, 2026-11-01, 2d
Adatbázis és UML tervezés     :active,  des2, after des1, 3d
section Backend fejlesztés (Spring Boot)
Entity és Repository réteg    :         dev1, after des2, 4d
Service réteg (Árkalkuláció)  :         dev2, after dev1, 4d
REST API Controllerek         :         dev3, after dev2, 3d
Security (RBAC) bekötése      :         dev4, after dev3, 3d
