# Point-of-Sale (PoS) System

[![Build Tool](https://img.shields.io/badge/Build-Maven-red.svg)](https://maven.apache.org/)
[![Language](https://img.shields.io/badge/Language-Java_17+-blue.svg)](https://www.oracle.com/java/)
[![GUI](https://img.shields.io/badge/GUI-JavaFX-green.svg)](https://openjfx.io/)
[![Database](https://img.shields.io/badge/Database-MySQL-blue.svg)](https://www.mysql.com/)
[![Methodology](https://img.shields.io/badge/Methodology-Scrum-orange.svg)](https://www.scrum.org/)

A retail management solution built using the **Maven** lifecycle and **JavaFX**. This project demonstrates a clean separation of concerns through layered architecture and implements core retail workflows including inventory tracking, sales transactions, and return processing.

## 🚀 Core Functionalities

### 1. Inventory & Stock Management
* **Dynamic Inventory**: Real-time tracking of stock levels with automatic status updates.
* **Product Registry**: Full management (Create, Read, Update, Delete) of product descriptions, and pricing.
* **Search Engine**: Filterable product lists for quick item lookup during manual entry.

### 2. Process Sales Workflow
* **Transaction Processing**: Virtual shopping cart that calculates sub-totals, taxes, and grand totals dynamically.
* **Payment Logic**: Handles cash payments, calculates precise change, and validates transaction completion.
* **Data Integrity**: Atomic updates ensure that stock levels are deducted immediately upon a successful sale.

### 3. Return & Refund System
* **Verification**: Validates returns against unique Receipt/Invoice IDs to prevent fraudulent claims.
* **Logic Constraints**: Ensures return quantities do not exceed original purchase amounts.
* **Inventory Restoration**: Automatically adds returned items back into the active stock count.

### 4. Management Reporting
* **Transaction Logs**: Persistent storage of all historical sales and returns.
* **Auditing**: Detailed view of specific transaction line items for managerial review.

## 🖼️ System Preview

| Inventory Management | Transaction History |
|---|---|
| ![Inventory](./images/inventory.jpg) | ![History](./images/history.jpg) |

| Process Sale | Handle Return |
|---|---|
| ![Process Sale](./images/process_sale.jpg) | ![Handle Return](./images/handle_return.jpg) |
