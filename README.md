NETWORK_PACKET_SNIFFER

NetworkPacketSniffer it's Open-Source project that give to the User all Utils that he need for his network for sniffing
and Scanning his network .

RUN
    firstly you need to include *.so in your LD_LIBRARY_PATH, then run the project using the makefile using tag all 
        'make all' .

HOW TO USE
    NetworkPacketSniffer give a lot of utils to the user, so after running the programme it's asking you to choose 
        an interface after that you can enter one of these commands :
        
        scan : it's scanning network and return all device conncted in your network in table that conting device name, ip
                and his mac Address .
        capture '-filter' ' filter_cmd' 'n': capture cmd it' allow you to capture number of packet that you want
                n it's the number of packet that you want to capture ,
                -filter its optional parameter it' when you want to apply some filter to packet that 
                you want to capture . Example : capture 10 -filter src 192.168.1.8
                for more detail about -filter check Man page of PCAP-FILTER
        all : that show you all packet that you capture 
        get 'id' : get return all information about some packet by passing his id that 
                   you will get in all cmd
        kickOff 'ip' : that cmd will kick off a device from network by passing his Ip
        devicesOff : show all device that you kickOff
        connect 'id' : connect the device that kickOff by his id wou will find him in 
                      devicesOff cmd
        exit: return 0 and quit  

