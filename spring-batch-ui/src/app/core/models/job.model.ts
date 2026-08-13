export interface JobExecution {

  id: number;

  jobName: string;

  templateId: number;

  filePath: string;

  status: JobExecutionStatus;

  startedAt: string | null;

  finishedAt: string | null;

  totalRecords: number | null;

  successRecords: number | null;

  errorRecords: number | null;

  requestedBy: string | null;

  coreExecutionId: string | null;

  createdAt: string;

}

export interface JobExecutionRequest {

  jobName: string;

  templateId: number;

  filePath: string;

  requestedBy?: string | null;

}

export interface JobExecutionError {

  id: number;

  lineNumber: number | null;

  lineContent: string | null;

  errorDescription: string;

  exceptionDetail: string | null;

  createdAt: string;

}

export interface JobReport {

  executionId: number;

  jobName: string;

  filePath: string;

  status: JobExecutionStatus;

  startedAt: string | null;

  finishedAt: string | null;

  durationSeconds: number | null;

  totalRecords: number | null;

  successRecords: number | null;

  errorRecords: number | null;

  successRate: number | null;

  errors: JobExecutionError[];

}

export enum JobExecutionStatus {

  PENDING = 'PENDING',

  RUNNING = 'RUNNING',

  COMPLETED = 'COMPLETED',

  FAILED = 'FAILED',

  STOPPED = 'STOPPED'

}
