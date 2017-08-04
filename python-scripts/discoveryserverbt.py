import json

from discoveryserver import *
import bluetooth


@ConndeHandler.register
class ConndeBluetoothHandler(ConndeHandler):
    def __init__(self, server, client_sck, client_info):
        ConndeHandler.__init__(self, server)
        self.client_sck = client_sck
        self.client_info = client_info

    def handle(self):
        log.info('Handling new connection to |%s|', str(self.client_info))
        log.info('Connection timeout is |%s|', str(self.client_sck.gettimeout()))

        msg = self._receive_msg()
        while msg:  # as long there are messages handle them
            self._handle_msg(msg)
            msg = self._receive_msg()

        # when no messages arrive, close the connection
        self.client_sck.close()

    def _receive_msg(self):
        log.debug('Trying to receive msg from |%s', str(self.client_info))
        msg = ''
        while True:
            try:
                data = self.client_sck.recv(1024)
                data = data.decode(const.ENCODING)
            except bluetooth.BluetoothError as bt_err:
                error_msg = str(bt_err)
                if '104' in error_msg:
                    log.info('Connection closed by client')
                    return False
                elif not error_msg.__eq__('timed out'):
                    raise bt_err

            if not data:  # on an empty string the connection was closed
                return False

            msg += data

            try:
                json_data = json.loads(msg)
                log.debug('Received message |%s| from |%s|', msg, self.client_sck.getpeername())
                return json_data
            except json.JSONDecodeError:
                log.debug('Could not load JSON object from |%s|', msg)

    def _send_msg(self, msg):
        msg_string = json.dumps(msg)
        log.debug('Sending message |%s| to |%s|', msg_string, str(self.client_info))
        self.client_sck.sendall(msg_string)


@ConndeServer.register
class ConndeBluetoothServer(ConndeServer):
    def __init__(self, RequestHandlerClass, db_client, service, poll_interval=0.5):
        ConndeServer.__init__(self, const.BT, db_client, service)
        self.RequestHandlerClass = RequestHandlerClass
        self.server_sck = bluetooth.BluetoothSocket()
        self.server_sck.settimeout(poll_interval)
        self.server_sck.bind(('', bluetooth.PORT_ANY))
        self.server_sck.listen(1)
        self._shutdown = False
        log.info('Started BT socket on RFCOMM channel %d', self.server_sck.getsockname()[1])
        bluetooth.advertise_service(self.server_sck,
                                    const.BT_SERVICE_NAME,
                                    service_id=const.BT_UUID,
                                    service_classes=[const.BT_UUID, bluetooth.SERIAL_PORT_CLASS],
                                    profiles=[bluetooth.SERIAL_PORT_PROFILE],
                                    description=const.BT_SERVICE_DESCRIPTION,
                                    protocols=[bluetooth.RFCOMM_UUID]
                                    )
        log.info('Advertising BT service')

    def serve_forever(self):
        global client_info
        while not self._shutdown:
            try:
                client_info = 'unknown'
                client_sck, client_info = self.server_sck.accept()
                handler = self.RequestHandlerClass(self, client_sck, client_info)
                handler.handle()
                client_sck.close()
            except bluetooth.BluetoothError as bt_err:
                error_msg = str(bt_err)
                if not error_msg.__eq__('timed out'):
                    log.exception('Exception while handling connection to |%s|', str(client_info))

        bluetooth.stop_advertising(self.server_sck)
        self.server_sck.close()

    def shutdown(self):
        self._shutdown = True
