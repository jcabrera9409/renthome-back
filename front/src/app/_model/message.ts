export class Message {
    status: string;
    message: string;
    error: any;

    constructor(status: string, message: string, error?: any) {
        this.status = status;
        this.message = message;
        this.error = error;
    }

    static error(message: string, error?: any): Message {
        return new Message('ERROR', message, error);
    }

    static success(message: string): Message {
        return new Message('SUCCESS', message);
    }

    static info(message: string): Message {
        return new Message('INFO', message);
    }

    static warning(message: string): Message {
        return new Message('WARNING', message);
    }
}
