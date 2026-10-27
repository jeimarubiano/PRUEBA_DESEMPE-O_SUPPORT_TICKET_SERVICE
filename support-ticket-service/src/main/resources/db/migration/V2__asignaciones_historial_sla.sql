CREATE INDEX idx_tickets_requester ON tickets(requester_id);
CREATE INDEX idx_tickets_technician ON tickets(technician_id);
CREATE INDEX idx_tickets_status_sla ON tickets(status, sla_due_at);
CREATE INDEX idx_ticket_histories_ticket ON ticket_histories(ticket_id);
