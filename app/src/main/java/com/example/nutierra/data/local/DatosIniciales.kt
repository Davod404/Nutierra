package com.example.nutierra.data.local

import com.example.nutierra.data.repository.UsuarioRepository
import com.example.nutierra.data.local.entity.ProductoEntity
import com.example.nutierra.data.local.entity.UsuarioEntity
import com.example.nutierra.data.local.entity.Roles

object DatosIniciales {

    val productos = listOf(
        ProductoEntity(
            id = 1,
            nombre = "Almendras Naturales 250 g",
            categoria = "Frutos secos",
            precio = 4990,
            stock = 24,
            peso = "250 g",
            imagenUri = "https://lh3.googleusercontent.com/aida-public/AB6AXuAz5dnzOkuHwlp8wpHDfLuIloxHRb2V9LOztdq2tz_MZ31sJ-Np6O-oeQeT_7Lv36cBybBvtBFIGSeA98h41MHYTlci03coveINDXQSglWmSk3U52NvP3N_WJEEJ5l_DgXHxnRrsEXiF4JAEtiUUhMfIFFehz7-GG6k9Hky26wspOnzYhQdRqYCRUXeBcFpfpX5PMUT9KUNv1DQhd5tA2WNmJAVDfKBnaO5jnpA9No",
            descripcion = "Almendras seleccionadas, sin sal añadida. Ideales para colaciones y recetas."
        ),
        ProductoEntity(
            id = 2,
            nombre = "Nueces Mariposa 250 g",
            categoria = "Frutos secos",
            precio = 5490,
            stock = 18,
            peso = "250 g",
            imagenUri = "https://lh3.googleusercontent.com/aida-public/AB6AXuCvcDOM1-IzpzTYZ9LdzEvaNrmKoLw_bQR55J36C603fQ7JA56yWl4K85h6cmcD2fcGMxs7ItRcjgOT9nmhQRl4AeE56yiUNGJQTg-pyRqLNMYhz2gvyR8Kuf_pyP4oVKy84kTqzmKB7PQMlUSwCIuEy6-1rlfb7htyC4PsyfLOlM2CZPNnNRFmIVcHBXLqDofIQdrlVpV739xzv5dfmA3H-Yu0Y-gr4fLKrJU9gmo",
            descripcion = "Nueces mariposa de cosecha nacional, crujientes y frescas."
        ),
        ProductoEntity(
            id = 3,
            nombre = "Maní Tostado 500 g",
            categoria = "Frutos secos",
            precio = 3290,
            stock = 32,
            peso = "500 g",
            imagenUri = "https://lh3.googleusercontent.com/aida-public/AB6AXuBlbJFLgNnJEviDRDzX-I8D0Hh9uEzOLY2GsEhF7eBQNFN-7EK_QMiji-nkyM5-SbhwEb-EOR2VVHiETq3W7sWfOk8EahVIYSSdyoRuNAZSTBQAzxvZsio_RO1BW3UcjEb-98t8nlN-P--5RDfXnh5lJJAAMWs4YRqcMFWkSMam5UpLXnmY9BJ9oHBF3XTxkxhu-BFpkJPtHabELGpPOe865925kSFzgWHUC8LNVcU",
            descripcion = "Maní tostado sin sal, perfecto para picar o preparar mantequilla."
        ),
        ProductoEntity(
            id = 4,
            nombre = "Semillas de Chía 250 g",
            categoria = "Semillas",
            precio = 2990,
            stock = 40,
            peso = "250 g",
            imagenUri = "https://lh3.googleusercontent.com/aida-public/AB6AXuCO4V0PXVODW2kteBFV4_L2nh49HnqEJcGVVJT8iXjWdvP8abQP_ZMBCTDAezTNBtLL0wmOID1IF19ie5K5AsHZzcsMGSeCMO-VYQ9fdxAzCfSdpFLIQAZTFkSHWyoOZPXX0z3_2eudq6m_SnVWrKHw5rPU9DKY96v3T5RvVQTUrANQKhIbMvXFwkKdREBfd0QxDLSDIkOa8DhNx3brvxR8ALRFcYoQy_BnxRqpB98",
            descripcion = "Semillas de chía ricas en fibra, ideales para batidos y yogurt."
        ),
        ProductoEntity(
            id = 5,
            nombre = "Semillas de Linaza 500 g",
            categoria = "Semillas",
            precio = 2490,
            stock = 15,
            peso = "500 g",
            imagenUri = "https://lh3.googleusercontent.com/aida-public/AB6AXuCfovxzO0Ua7AVwgcveHSw9Il5XjqaI3iGw9_wLHQMfAPuZk0Arzr3KiRdSeq_tCUvssHJ_Wt_OYD_35yrPu2S6KYS5d1Kh_SSp3foUMcJDTRD5n5X3lkDyhxMTwo9MtCtfTyV0hjrtzoA4Ffr8kBQl1pdwdPm0QcHA6NUAcKUnKrFfTzOMXf0CX75ZLP1tj1spySxr0k4pSe45SwM8SiVAKZyGRXCmq85dcOEB39U",
            descripcion = "Linaza dorada para panes, ensaladas y cereales."
        ),
        ProductoEntity(
            id = 6,
            nombre = "Pasas Rubias 250 g",
            categoria = "Deshidratados",
            precio = 2790,
            stock = 21,
            peso = "250 g",
            imagenUri = "https://lh3.googleusercontent.com/aida-public/AB6AXuBlPYN02qcN-0-brRALT0bpbq8bW5fTKwJZKXpV5_MZmteXk3d5xD46Wo9dL5XdmshoTZXYin0syhCz-qnG57Qqljrh4snfW37TI1OSUuuPy6oI7_WJo2RlB2q4LrJdnTXm1Qt7F3FC1chGRgDypi1PwEJ6IbACmluCCtuHrOn_tkaV1IYsWvtvbSaJHfRZvivu67m9nYXts4jy1boryioHdaGbjQOoiONo2bnD1a8",
            descripcion = "Pasas rubias dulces y jugosas, sin azúcar añadida."
        ),
        ProductoEntity(
            id = 7,
            nombre = "Cranberries 250 g",
            categoria = "Deshidratados",
            precio = 4290,
            stock = 0,
            peso = "250 g",
            imagenUri = "https://lh3.googleusercontent.com/aida-public/AB6AXuC2-hJqCt-CTdqksXXJ5-vo2oIw5UIbSZ3zKkraUdZPC7aSR5wakWQ5R0yWjkF7AGqZ3ESKKT71KSr4jJiZ1Cbonetqveol0y-GutywnLRWjA_CbYBG0wApEfAAfZAmD90Sxggy9pFtvr-tDjJnm-1DafYMrRW_sjWyU31PfpyVG5-qszYkLr9EBdjy7e4cU0J-P_bmPVjh5rH5bgdWHvjE_o4NgHQCbakOIR3vafI",
            descripcion = "Cranberries deshidratados, perfectos para mezclas de frutos secos."
        ),
        ProductoEntity(
            id = 8,
            nombre = "Damascos Deshidratados 250 g",
            categoria = "Deshidratados",
            precio = 3990,
            stock = 12,
            peso = "250 g",
            imagenUri = "https://lh3.googleusercontent.com/aida-public/AB6AXuA7sllKwRO-DYMPsQJvXsmmjjW4D7sEuRmegHemGm7-csHoScfhHEYR88PJNdYLr0jTH7D72Pn0qkRg7wd6f7gY7-XERs0w3T4ULG1fbjI-dqdh89CbWOUZ-yAuTGryqKtv8GKKzv-AB31P2pRkbNinKgh4QUge8ci6Jooe0M18t-pIFNLLXLa2rsiuTawBaUmJN0NqqZQr9ataGVqrMwaBD8nx4VUWGbsD0eywOEs",
            descripcion = "Damascos deshidratados de textura suave, sin preservantes."
        )
    )

    val usuarios = listOf(
        UsuarioEntity(
            nombre = "Administrador",
            apellido = "Nutierra",
            correo = "admin@nutierra.cl",
            contrasenaHash = UsuarioRepository.hash("AdminMaster2025!"),
            rol = Roles.ADMIN
        ),
        UsuarioEntity(
            nombre = "Cliente",
            apellido = "Demo",
            correo = "cliente@nutierra.cl",
            contrasenaHash = UsuarioRepository.hash("Semillas2025!"),
            rol = Roles.CLIENTE
        )
    )
}
