download() {
  echo "Downloading: $1 into $RESTORE_PATH"

  # User-provided backup filename (optional)
  INPUT_BACKUP_NAME="mybackupfile"

  if [ -n "$INPUT_BACKUP_NAME" ]; then
    echo "Specific backup input provided: $INPUT_BACKUP_NAME"

    # Try to find the exact or matching backup in S3
    MATCHING_FILE=$(s3cmd ls "s3://${S3BUCKET}/solrbackup/${SRC_BACKUP}/" | grep "$INPUT_BACKUP_NAME" | grep '.tar.gz' | awk '{print $NF}' | sort | tail -n 1)

    if [ -z "$MATCHING_FILE" ]; then
      echo "No matching file found for input: $INPUT_BACKUP_NAME"
      log "Error: No matching backup found for input: $INPUT_BACKUP_NAME"
      exit 1
    fi

    SRC_FILE=$(basename "$MATCHING_FILE")
    echo "Found matching backup: $SRC_FILE"

  else
    echo "No input provided. Getting the latest file modified in the last 9 days..."
    
    # Get the latest file modified in the last 9 days
    LATEST_FILE=$(s3cmd ls "s3://${S3BUCKET}/solrbackup/${SRC_BACKUP}/" | \
      grep '.tar.gz' | \
      awk -v date="$(date -d '9 days ago' +%Y-%m-%d)" '$1 >= date {print $NF}' | \
      sort | tail -n 1)

    if [ -z "$LATEST_FILE" ]; then
      log "No SOLR Backup files found in last 9 days, Location: s3://${S3BUCKET}/solrbackup/"
      echo "No SOLR Backup files found in last 9 days" | mail -s "Error: No Recent Files" "$EMAIL"
      exit 1
    fi

    SRC_FILE=$(basename "$LATEST_FILE")
    echo "Latest backup file selected: $SRC_FILE"
  fi

  # Actual download
  echo "Downloading s3://${S3BUCKET}/solrbackup/${SRC_BACKUP}/${SRC_FILE} to $RESTORE_PATH"
  s3cmd get "s3://${S3BUCKET}/solrbackup/${SRC_BACKUP}/${SRC_FILE}" "$RESTORE_PATH/"
}
