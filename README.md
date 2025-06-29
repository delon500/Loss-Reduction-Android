# Loss Reduction System

## Overview

The **Loss Reduction System** (LRS) is a full-stack solution designed to help organizations manage assets and expenses across multiple clients and projects. It provides features for tracking, reporting, and reconciling assets and expenses, as well as generating budgets and invoices.

## Features

- **Client & Project Selection**: Users select a client, then a project within that client.
- **Asset Management**:
    - Add, update, and view assets with images
    - Filter assets by name, category, status, and quantity
    - Track asset details such as value, date added, and status
- **Expense Management**:
    - Record expenses (fuel, material usage, stationery, etc.)
    - Attach scanned receipts
    - Monitor monthly expense totals per vehicle or category
- **Budgeting & Reporting**:
    - Create monthly budgets per project
    - Reconcile actual vs. target expenses
    - Generate detailed reports per item or category
- **Authentication & Authorization**:
    - Admin login with roles
    - Secure API endpoints

## Architecture

- **Backend**: PHP (with PDO), MySQL
- **Frontend**: Android app (Java + Volley + Material UI)

## Prerequisites

- PHP 7.4+ with PDO extension
- MySQL 5.7+
- Android Studio (for the mobile app)
- XAMPP or similar local PHP/MySQL environment

## Installation

### Backend

1. Clone the repository to your server:
   ```bash
   git clone https://github.com/delon500/Loss-Reduction-Backend.git
   cd loss-reduction-system/backend
   ```
2. Import the database schema (`loss_reduction_db.sql`) into MySQL:
   ```bash
   mysql -u root -p loss_reduction_db < loss_reduction_db.sql
   ```
3. Configure database connection in `db_connection.php`:
   ```php
   $host = "localhost";
   $user = "root";
   $password = "";
   $database = "loss_reduction_db";
   ```
4. Ensure the `uploads/assets` directory exists and is writable:
   ```bash
   mkdir -p uploads/assets
   chmod 775 uploads/assets
   ```
5. Start your PHP server (e.g., via XAMPP or `php -S localhost:8000`).

### Android App

1. Open Android Studio and import the project located in `android-app/`.
2. Update the backend base URL in `LeadingPage.java` and `AssetFragment.java` if needed:
   ```java
   private static final String BASE_URL = "http://10.0.2.2/loss_reduction_backend/";
   ```
3. Build and run the app on an emulator or device.

## Database Schema Highlights

- **clients**: `(id, name)`
- **projects**: `(id, client_id, name, description)`
- **categories**: `(id, name, type)`
- **assets**: `(id, client_id, project_id, category_id, asset_name, quantity, value, image_path, status)`
- **asset\_statuses**: `(code, label, color_hex)`
- **expenses**: `(id, client_id, project_id, category_id, amount, date, receipt_path)`

## API Endpoints

- `GET /get_clients.php` – List all clients
- `GET /get_projects.php?client_id={id}` – List projects for a client
- `GET /get_assets.php?client_id={}&project_id={}` – List assets
- `POST /add_asset.php` – Create a new asset (multipart/form-data)
- `POST /login.php` – Admin authentication

