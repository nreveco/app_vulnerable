To secure your Dockerfile and remove hardcoded secrets, you can use environment variables or secret management systems like Vault. Here's an example of using environment variables:

```
FROM python:3.8-slim-buster
WORKDIR /app
COPY requirements.txt .
cmd -v pip install --no-cache-dir -r requirements.txt

# Load environment variables
import os

DB_HOST = os.getenv('DB_HOST')
DB_USER = os.getenv('DB_USER')
DB_PASS = os.getenv('DB_PASS')

# Use the secrets in your code
pymysql.connect(db=DB_NAME, user=DB_USER, password=DB_PASS, host=DB_HOST)
```
