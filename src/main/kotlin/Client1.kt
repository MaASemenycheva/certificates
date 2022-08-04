import io.grpc.ManagedChannelBuilder

suspend fun main() {
    val port = 8080

    val channel = ManagedChannelBuilder.forAddress("localhost", port).usePlaintext().build()
    val stub = RegistrationServiceGrpcKt.RegistrationServiceCoroutineStub(channel)
    val data = cert {
        certificate = """-----BEGIN CERTIFICATE-----
                        MIIFyDCCBXOgAwIBAgIQQGAcMPNxUXkTUJL0YVGsVTAMBggqhQMHAQEDAgUAMEkx
                        CzAJBgNVBAYTAlJVMQswCQYDVQQIDAIwMDENMAsGA1UEBwwEQ0JEQzEMMAoGA1UE
                        CgwDQ0JSMRAwDgYDVQQDDAdBRE1JTkNBMB4XDTIxMDkyNzExMzQ0NVoXDTI3MTIy
                        MTA3NDgwM1owgesxCzAJBgNVBAYTAlJVMQ0wCwYDVQQHDARDQkRDMUQwQgYDVQQK
                        DDt0ZXN0LnJ1LmNicmRjLnBydC5QcnNuLmNhMzYzZThjLTEyZWUtNGY1NC05ZDU3
                        LWQ1Nzk0NmJlYWVkOTFDMEEGA1UECww6dGVzdC5ydS5jYnJkYy53bHQuQ2x0LmQz
                        MDk2ZGU0LTY3MTQtNDQ1OS04ODc3LTc2NjBjMDg3MDNmYTFCMEAGA1UEAww5dGVz
                        dC5ydS5jYnJkYy5wcnQuRkkuMjAwOGZiM2QtNDQ0ZS00OTdhLWEwYzEtZTg5NWMy
                        ZjMxYTk0MGYwHwYIKoUDBwEBAQEwEwYHKoUDAgIkAAYIKoUDBwEBAgIDQwAEQBbe
                        w86SnI366IiYQmAq3sJL2IsLSGihciQrYtQ3uwn41cKctQXI94W1o4pvWmCtwBXy
                        cs/9qYDBGMza3pIRUP+jggOJMIIDhTCB4wYDVR0RBIHbMIHYoIHVBgNVBAqggc0M
                        gcrQmtC70LjQtdC90YIg0J/Qu9Cw0YLRhNC+0YDQvNGLINGG0LjRhNGA0L7QstC+
                        0LPQviDRgNGD0LHQu9GPLCDQvtCx0YHQu9GD0LbQuNCy0LDRjtGJ0LjQudGB0Y8g
                        0YMg0KTQuNC90LDQvdGB0L7QstC+0LPQviDQv9C+0YHRgNC10LTQvdC40LrQsCDC
                        q3Rlc3QucnUuY2JyZGMucHJ0LkZJLjIwMDhmYjNkLTQ0NGUtNDk3YS1hMGMxLWU4
                        OTVjMmYzMWE5NMK7MA4GA1UdDwEB/wQEAwIDyDAMBgNVHRMBAf8EAjAAMCIGA1Ud
                        JQQbMBkGCisGAQQB0AQEAgEGCysGAQQB0AQHMgMBMCgGCSsGAQQB0AQEAwQbMBkG
                        CSsGAQQB0AQFAwQMOTEwNEpOUTc1OTAxMCsGA1UdEAQkMCKADzIwMjEwOTI3MTEz
                        NDQ1WoEPMjAyMjEyMjcxMTM0NDVaMB0GA1UdDgQWBBQhVuYADaxJRrQK2OurYyFz
                        3BX9vDCCAR0GBSqFA2RwBIIBEjCCAQ4MUtCQ0J/QmiAi0KHQuNCz0L3QsNGC0YPR
                        gNCwLdC60LvQuNC10L3RgiBMIiDQstC10YDRgdC40Y8gNiAo0LjRgdC/0L7Qu9C9
                        0LXQvdC40LUgMykMWtCQ0J/QmiAi0KHQuNCz0L3QsNGC0YPRgNCwLdGB0LXRgNGC
                        0LjRhNC40LrQsNGCIEwiINCy0LXRgNGB0LjRjyA2ICjQuNGB0L/QvtC70L3QtdC9
                        0LjQtSAyKQwt0KHQpC8xMDAtMDAwMCDQvtGCIDMxINC00LXQutCw0LHRgNGPIDIw
                        MjAg0LMuDC3QodCkLzEwMC0wMDAwINC+0YIgMzEg0LTQtdC60LDQsdGA0Y8gMjAy
                        MCDQsy4wgYAGA1UdIwR5MHeAFLfHnGTG14WUZ11lUr4Mxurbuo8zoU2kSzBJMQsw
                        CQYDVQQGEwJSVTELMAkGA1UECAwCMDAxDTALBgNVBAcMBENCREMxDDAKBgNVBAoM
                        A0NCUjEQMA4GA1UEAwwHQURNSU5DQYIQQGAcMIYqHdIAUpz+YUmOLjBBBgNVHRIE
                        OjA4oB4GA1UECqAXDBXQkdCw0L3QuiDQoNC+0YHRgdC40LigFgYDVQQNoA8MDdCh
                        0LXRgNC40Y8gMDEwDAYIKoUDBwEBAwIFAANBAO4NX5eAHeh10oGPK4VSjAse7Fix
                        j0RrtQ6Zuqe8yEBnFWqk2y9BrGw6TP+qhXZZeb0VTG8lxZUv0Zq0mpf5OUM=
                        -----END CERTIFICATE-----"""
    }
    val result = stub.register(data)
    print("Success is ${result.succeeded}")

}