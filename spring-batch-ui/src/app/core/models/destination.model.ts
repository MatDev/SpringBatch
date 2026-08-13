export interface Destination {

  id: number;

  destinationType: DestinationType;

  name: string;

  targetTable: string | null;

  targetCollection: string | null;

  targetTopic: string | null;

  targetPath: string | null;

  targetUrl: string | null;

  active: boolean;

  extraConfig: Record<string, unknown> | null;

  mappings: DestinationMapping[];

}

export interface DestinationRequest {

  destinationType: DestinationType;

  name: string;

  targetTable?: string | null;

  targetCollection?: string | null;

  targetTopic?: string | null;

  targetPath?: string | null;

  targetUrl?: string | null;

  active: boolean;

  extraConfig?: Record<string, unknown> | null;

}

export interface DestinationMapping {

  id: number;

  fieldName: string;

  targetColumn: string;

  transformExpression: string | null;

  orderIndex: number | null;

}

export enum DestinationType {

  POSTGRESQL = 'POSTGRESQL',

  MONGODB = 'MONGODB',

  KAFKA = 'KAFKA',

  FILE = 'FILE',

  REST = 'REST'

}
