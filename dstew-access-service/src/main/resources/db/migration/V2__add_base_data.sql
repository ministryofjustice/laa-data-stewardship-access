-- Insert into application
INSERT INTO applications (
    id, provider_firm_id, provider_office_id, client_id,
    status_code, statement_of_case, is_emergency_application,
    created_at, created_by, updated_at, updated_by
) VALUES (
             '123e4567-e89b-12d3-a456-426614174000',  -- fixed UUID for clarity
             'firm-001',
             'office-101',
             '321e4567-e89b-12d3-a456-426614174999',
             'NEW',
             'Client needs emergency housing support',
             TRUE,
             '2025-05-08T10:00:00Z', 'admin', '2025-05-08T10:00:00Z', 'admin'
         );

-- Insert into application_proceeding
INSERT INTO applications_proceedings (
    id, application_id, proceeding_code, level_of_service_code,
    created_at, created_by, updated_at, updated_by
) VALUES
      (
          '456e4567-e89b-12d3-a456-426614174111',
          '123e4567-e89b-12d3-a456-426614174000',
          'FAM01',
          'FULLREP',
          '2025-05-08T10:00:00Z', 'admin', '2025-05-08T10:00:00Z', 'admin'
      ),
      (
          '789e4567-e89b-12d3-a456-426614174222',
          '123e4567-e89b-12d3-a456-426614174000',
          'FAM02',
          'HELP',
          '2025-05-08T10:00:00Z', 'admin', '2025-05-08T10:00:00Z', 'admin'
      );

INSERT INTO application_history (
    id,
    application_id,
    user_id,
    action,
    resource_type_changed,
    timestamp,
    application_snapshot
) VALUES (
             'abc14567-e89b-12d3-a456-426614174999',
             '123e4567-e89b-12d3-a456-426614174000',
             'admin',
             'CREATED',
             'APPLICATION',
             '2025-05-08T10:00:00Z',
             '{
                 "id": "123e4567-e89b-12d3-a456-426614174000",
                 "provider_firm_id": "firm-001",
                 "provider_office_id": "office-101",
                 "client_id": "321e4567-e89b-12d3-a456-426614174999",
                 "status_code": "NEW",
                 "statement_of_case": "Client needs emergency housing support",
                 "is_emergency_application": true,
                 "created_at": "2025-05-08T10:00:00Z",
                 "created_by": "admin",
                 "updated_at": "2025-05-08T10:00:00Z",
                 "updated_by": "admin",
                 "proceedings": [
                     {
                         "id": "456e4567-e89b-12d3-a456-426614174111",
                         "application_id": "123e4567-e89b-12d3-a456-426614174000",
                         "proceeding_code": "FAM01",
                         "level_of_service_code": "FULLREP",
                         "created_at": "2025-05-08T10:00:00Z",
                         "created_by": "admin",
                         "updated_at": "2025-05-08T10:00:00Z",
                         "updated_by": "admin"
                     },
                     {
                         "id": "789e4567-e89b-12d3-a456-426614174222",
                         "application_id": "123e4567-e89b-12d3-a456-426614174000",
                         "proceeding_code": "FAM02",
                         "level_of_service_code": "HELP",
                         "created_at": "2025-05-08T10:00:00Z",
                         "created_by": "admin",
                         "updated_at": "2025-05-08T10:00:00Z",
                         "updated_by": "admin"
                     }
                 ]
             }'::jsonb
         );
