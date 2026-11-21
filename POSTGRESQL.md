# PostgreSQL setup

The application uses PostgreSQL at runtime. Configure these environment variables locally or in the Render service's environment settings:

- `DATABASE_URL` (required JDBC URL; for Supabase use the transaction-mode pooler on port `6543` with SSL required)
- `DATABASE_USERNAME` (required database user)
- `DATABASE_PASSWORD` (required database password)
- `JWT_SECRET` (required signing secret; use a long, randomly generated value)
- `PORT` (provided by Render; defaults to `8080` for local runs)

For example, in PowerShell:

```powershell
$env:DATABASE_URL = "jdbc:postgresql://aws-1-ap-south-1.pooler.supabase.com:6543/postgres?sslmode=require"
$env:DATABASE_USERNAME = "postgres.<project-ref>"
$env:DATABASE_PASSWORD = "<your-postgres-password>"
$env:JWT_SECRET = "<a-long-random-secret>"
.\mvnw.cmd spring-boot:run
```

For Render, create a **Web Service** from this repository and select **Docker** as the runtime. Set the required variables above in the service's **Environment** settings; Render supplies `PORT` automatically. Do not commit credentials or secrets to source files. For Supabase, use the transaction-mode pooler URL on port `6543`; the direct/session-mode URL on port `5432` is intended for migration tools, not this application runtime. Hibernate updates the schema on application startup. The current unit tests do not connect to a database.
